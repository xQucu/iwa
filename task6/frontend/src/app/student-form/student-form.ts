import { Component, inject, OnInit, signal } from "@angular/core";
import { Student } from "../models/student";
import { StudentService } from "../services/studentService";

@Component({
  selector: "app-student-form",
  imports: [],
  templateUrl: "./student-form.html",
  styleUrl: "./student-form.css",
})
export class StudentForm implements OnInit {
  private studentService = inject(StudentService);

  studentList = signal<Student[]>([]);

  ngOnInit() {
    this.getStudents();
  }

  getStudent(
    id: number,
    firstNameInput: HTMLInputElement,
    lastNameInput: HTMLInputElement,
    emailInput: HTMLInputElement,
    telephoneInput: HTMLInputElement,
  ): void {
    if (!id) return;
    this.studentService.getStudent(id).subscribe({
      next: (newStudent) => {
        firstNameInput.value = newStudent.firstName || "";
        lastNameInput.value = newStudent.lastName || "";
        emailInput.value = newStudent.email || "";
        telephoneInput.value = newStudent.number || "";
      },
      error: () => {},
      complete: () => {},
    });
  }

  getStudents(): void {
    this.studentService.getStudents().subscribe({
      next: (newStudentList) => {
        this.studentList.set(newStudentList);
      },
      error: () => {},
      complete: () => {},
    });
  }

  add(
    firstName: string,
    lastName: string,
    email: string,
    number: string,
  ): void {
    firstName = firstName.trim();
    lastName = lastName.trim();
    email = email.trim();
    number = number.trim();
    this.studentService
      .addStudent({ firstName, lastName, email, number } as Student)
      .subscribe({
        next: (newStudent: Student) => {
          this.studentList.update((currentList: Student[]) => [
            ...currentList,
            newStudent,
          ]);
        },
        error: () => {},
        complete: () => {},
      });
  }

  delete(student: Student): void {
    this.studentService.deleteStudent(student).subscribe(() => {
      this.studentList.update((currentList) =>
        currentList.filter((c) => c !== student),
      );
    });
  }

  deleteAll(): void {
    this.studentService.deleteStudents().subscribe(() => {
      this.studentList.update((currentList) => {
        console.log(`Deleting ${currentList.length} students.`);
        return [];
      });
      // or use .set() method
      // this.studentList.set([]);
    });
  }

  update(
    firstName: string,
    lastName: string,
    email: string,
    number: string,
    chosenToUpdateStudent: Student,
  ): void {
    let id = chosenToUpdateStudent.id;
    firstName = firstName.trim();
    lastName = lastName.trim();
    email = email.trim();
    number = number.trim();
    console.log(id);
    if (id != undefined) {
      this.studentService
        .updateStudent({ firstName, lastName, email, number } as Student, id)
        .subscribe({
          next: (updatedStudent: Student) => {
            this.studentList.update((currentStudents) =>
              currentStudents.map((currentStudent) =>
                currentStudent.id === updatedStudent.id
                  ? updatedStudent
                  : currentStudent,
              ),
            );
          },
          error: () => {},
          complete: () => {},
        });
    }
  }

  patch(
    firstName: string,
    lastName: string,
    email: string,
    number: string,
    chosenToUpdateStudent: Student,
  ): void {
    let id = chosenToUpdateStudent.id;
    const updates: Partial<Student> = {};
    if (firstName.trim()) updates.firstName = firstName.trim();
    if (lastName.trim()) updates.lastName = lastName.trim();
    if (email.trim()) updates.email = email.trim();
    if (number.trim()) updates.number = number.trim();
    if (id != undefined && Object.keys(updates).length > 0) {
      this.studentService.patchStudent(id, updates).subscribe({
        next: (updatedStudent: Student) => {
          this.studentList.update((currentStudents) =>
            currentStudents.map((currentStudent) =>
              currentStudent.id === updatedStudent.id
                ? updatedStudent
                : currentStudent,
            ),
          );
        },
        error: () => {},
        complete: () => {},
      });
    }
  }

  replaceAll(input: string): void {
    const students: Student[] = input
      .split("\n")
      .filter((line) => line.trim())
      .map((line) => {
        const parts = line.trim().split(/\s+/);
        return {
          firstName: parts[0] || "",
          lastName: parts[1] || "",
          email: parts[2] || "",
          number: parts[3] || "",
        } as Student;
      });
    this.studentService.replaceAllStudents(students).subscribe({
      next: (newList) => {
        this.studentList.set(newList);
      },
      error: () => {},
      complete: () => {},
    });
  }
}
