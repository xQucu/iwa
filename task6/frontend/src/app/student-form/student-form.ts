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
}
