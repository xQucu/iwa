import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GradeService } from '../services/grade.service';
import { SubjectService } from '../services/subject.service';
import { Subject } from '../models/subject';
import { Grade } from '../models/grade';

@Component({
  selector: 'app-teacher',
  imports: [RouterLink, FormsModule],
  templateUrl: './teacher.html',
  styleUrl: './teacher.css',
})
export class Teacher implements OnInit {
  grades = signal<Grade[]>([]);
  subjects = signal<Subject[]>([]);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');

  studentIdInput = signal<number | null>(null);
  subjectIdInput = signal<number | null>(null);
  valueInput = signal<number>(5.0);
  descriptionInput = signal<string>('');

  private gradeService = inject(GradeService);
  private subjectService = inject(SubjectService);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.gradeService.getGrades().subscribe({
      next: (data) => this.grades.set(data),
      error: (err) => console.error(err)
    });
    this.subjectService.getSubjects().subscribe({
      next: (data) => {
        this.subjects.set(data);
        if (data.length > 0) {
          this.subjectIdInput.set(data[0].id || null);
        }
      },
      error: (err) => console.error(err)
    });
  }

  onSubmit() {
    const sId = this.studentIdInput();
    const subId = this.subjectIdInput();
    const val = this.valueInput();
    const desc = this.descriptionInput();

    if (!sId || !subId) {
      this.errorMessage.set('Please provide a Student ID and select a Subject.');
      return;
    }

    this.gradeService.addGrade({
      studentId: sId,
      subjectId: subId,
      value: val,
      description: desc
    }).subscribe({
      next: (newGrade) => {
        if (newGrade && newGrade.id) {
          this.successMessage.set('Grade added successfully!');
          this.errorMessage.set('');
          this.grades.update(list => [...list, newGrade]);
          this.studentIdInput.set(null);
          this.descriptionInput.set('');
          setTimeout(() => this.successMessage.set(''), 3000);
        } else {
          this.errorMessage.set('Failed to add grade. Make sure student ID is valid.');
        }
      },
      error: (err) => {
        this.errorMessage.set('Error submitting grade.');
        console.error(err);
      }
    });
  }
}
