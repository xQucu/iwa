import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Subject } from "../models/subject";
import { SubjectService } from "../services/subject.service";
import { TokenStorageService } from "../auth/token-storage-service";

@Component({
  selector: "app-student-form",
  imports: [RouterLink],
  templateUrl: "./student-form.html",
  styleUrl: "./student-form.css",
})
export class StudentForm implements OnInit {
  private subjectService = inject(SubjectService);
  private tokenStorage = inject(TokenStorageService);

  subjectList = signal<Subject[]>([]);
  isTeacher = signal<boolean>(false);
  isAdmin = signal<boolean>(false);
  isLoggedIn = signal<boolean>(false);
  username = signal<string>("");

  ngOnInit() {
    this.isLoggedIn.set(!!this.tokenStorage.getToken() && this.tokenStorage.getToken() !== '{}');
    if (this.isLoggedIn()) {
      this.username.set(this.tokenStorage.getUsername());
      const roles = this.tokenStorage.getAuthorities();
      this.isTeacher.set(roles.includes("ROLE_TEACHER"));
      this.isAdmin.set(roles.includes("ROLE_ADMIN"));
      this.getSubjects();
    }
  }


  getSubjects(): void {
    this.subjectService.getSubjects().subscribe({
      next: (list) => {
        this.subjectList.set(list);
      },
      error: (err) => console.error(err),
    });
  }

  add(name: string): void {
    name = name.trim();
    if (!name) return;
    this.subjectService.addSubject({ name } as Subject).subscribe({
      next: (addedSubject) => {
        if (addedSubject) {
          this.subjectList.update((list) => [...list, addedSubject]);
        }
      },
    });
  }

  delete(subject: Subject): void {
    if (subject.id === undefined) return;
    this.subjectService.deleteSubject(subject.id).subscribe({
      next: () => {
        this.subjectList.update((list) => list.filter((s) => s.id !== subject.id));
      },
    });
  }

  update(name: string, subject: Subject): void {
    name = name.trim();
    if (!name || subject.id === undefined) return;
    this.subjectService.updateSubject(subject.id, { name } as Subject).subscribe({
      next: (updatedSubject) => {
        if (updatedSubject) {
          this.subjectList.update((list) =>
            list.map((s) => (s.id === updatedSubject.id ? updatedSubject : s))
          );
        }
      },
    });
  }

  logout() {
    this.tokenStorage.signOut();
    window.location.reload();
  }
}

