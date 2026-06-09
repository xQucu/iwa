import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { GradeService } from "../services/grade.service";
import { SubjectService } from "../services/subject.service";
import { Subject } from "../models/subject";
import { Grade } from "../models/grade";
import { UserService } from "../services/user-service";
import { User } from "../models/user";

@Component({
  selector: "app-teacher",
  imports: [RouterLink, FormsModule],
  templateUrl: "./teacher.html",
  styleUrl: "./teacher.css",
})
export class Teacher implements OnInit {
  grades = signal<Grade[]>([]);
  subjects = signal<Subject[]>([]);
  students = signal<User[]>([]);
  errorMessage = signal<string>("");
  successMessage = signal<string>("");

  studentIdInput = signal<number | null>(null);
  subjectIdInput = signal<number | null>(null);
  valueInput = signal<number>(5.0);
  descriptionInput = signal<string>("");

  enrolStudentIdInput = signal<number | null>(null);
  enrolSubjectIdInput = signal<number | null>(null);

  searchQuery = signal<string>("");
  sortBy = signal<"student" | "subject">("student");

  onSearch(query: string) {
    this.searchQuery.set(query);
  }

  sortedGrades() {
    const list = [...this.grades()];
    const key = this.sortBy();
    if (key === "student") {
      return list.sort((a, b) =>
        (a.student?.username || "").localeCompare(b.student?.username || ""),
      );
    } else {
      return list.sort((a, b) =>
        (a.subject?.name || "").localeCompare(b.subject?.name || ""),
      );
    }
  }

  onReset() {
    this.valueInput.set(5.0);
    this.descriptionInput.set("");
    this.studentIdInput.set(null);
    this.subjectIdInput.set(null);
  }

  filteredSubjectsForList() {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.subjects();
    return this.subjects().filter((s) => s.name.toLowerCase().includes(q));
  }

  private gradeService = inject(GradeService);
  private subjectService = inject(SubjectService);
  private userService = inject(UserService);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.gradeService.getGrades().subscribe({
      next: (data) => this.grades.set(data),
      error: (err) => console.error(err),
    });
    this.userService.getUsers().subscribe({
      next: (data) => {
        const studentUsers = data.filter((u) =>
          u.roles.some((r) => r.name === "ROLE_STUDENT"),
        );
        this.students.set(studentUsers);
        if (studentUsers.length > 0) {
          const firstStudentId = studentUsers[0].id;
          this.enrolStudentIdInput.set(firstStudentId);
        }
      },
      error: (err) => console.error(err),
    });
    this.subjectService.getSubjects().subscribe({
      next: (data) => {
        this.subjects.set(data);
      },
      error: (err) => console.error(err),
    });
  }

  filteredSubjectsForSelectedStudent() {
    const selectedStudentId = this.studentIdInput();
    if (!selectedStudentId) return [];
    return this.subjects().filter((subject) =>
      subject.enrolledUsers?.some((user) => user.id == selectedStudentId),
    );
  }

  onStudentChange(studentId: number) {
    this.studentIdInput.set(studentId);
    const filtered = this.filteredSubjectsForSelectedStudent();
    if (filtered.length > 0) {
      this.subjectIdInput.set(filtered[0].id || null);
    } else {
      this.subjectIdInput.set(null);
    }
  }

  updateInitialSubjectSelection() {
    const studentId = this.studentIdInput();
    if (studentId && this.subjects().length > 0) {
      const filtered = this.filteredSubjectsForSelectedStudent();
      if (filtered.length > 0) {
        this.subjectIdInput.set(filtered[0].id || null);
      } else {
        this.subjectIdInput.set(null);
      }
    }
  }

  onSubmit() {
    const sId = this.studentIdInput();
    const subId = this.subjectIdInput();
    const val = this.valueInput();
    const desc = this.descriptionInput();

    if (!sId || !subId) {
      this.errorMessage.set(
        "Please provide a Student ID and select a Subject.",
      );
      return;
    }

    this.gradeService
      .addGrade({
        studentId: sId,
        subjectId: subId,
        value: val,
        description: desc,
      })
      .subscribe({
        next: (newGrade) => {
          if (newGrade && newGrade.id) {
            this.successMessage.set("Grade added successfully!");
            this.errorMessage.set("");
            this.grades.update((list) => [...list, newGrade]);
            this.onReset();
            setTimeout(() => this.successMessage.set(""), 3000);
          } else {
            this.errorMessage.set(
              "Failed to add grade. Make sure student ID is valid.",
            );
          }
        },
        error: (err) => {
          this.errorMessage.set("Error submitting grade.");
          console.error(err);
        },
      });
  }

  onEnrol() {
    const studentId = this.enrolStudentIdInput();
    const subjectId = this.enrolSubjectIdInput();
    if (!studentId || !subjectId) return;

    this.subjectService.enrolUser(subjectId, studentId).subscribe({
      next: () => {
        this.successMessage.set(`Successfully enrolled student ${studentId}.`);
        this.errorMessage.set("");
        this.loadData();
        setTimeout(() => this.successMessage.set(""), 3000);
      },
      error: (err) => {
        this.errorMessage.set(`Failed to enrol student ${studentId}.`);
        console.error(err);
      },
    });
  }

  onUnenrolForSubject(subjectId?: number, studentId?: number) {
    if (!studentId || !subjectId) return;

    this.subjectService.unenrolUser(subjectId, studentId).subscribe({
      next: () => {
        this.successMessage.set(`Successfully un-enrolled student.`);
        this.errorMessage.set("");
        this.loadData();
        setTimeout(() => this.successMessage.set(""), 3000);
      },
      error: (err) => {
        this.errorMessage.set(`Failed to un-enrol student.`);
        console.error(err);
      },
    });
  }
}
