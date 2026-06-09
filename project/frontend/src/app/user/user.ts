import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { GradeService } from "../services/grade.service";
import { Grade } from "../models/grade";

@Component({
  selector: "app-user",
  imports: [RouterLink],
  templateUrl: "./user.html",
  styleUrl: "./user.css",
})
export class User implements OnInit {
  grades = signal<Grade[]>([]);
  errorMessage = signal<string>("");

  private gradeService = inject(GradeService);

  ngOnInit() {
    this.gradeService.getGrades().subscribe({
      next: (data) => {
        this.grades.set(data);
      },
      error: (error) => {
        this.errorMessage.set("Failed to load grades.");
        console.error(error);
      },
    });
  }
}
