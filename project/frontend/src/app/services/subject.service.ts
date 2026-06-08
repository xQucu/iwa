import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject } from '../models/subject';

@Injectable({
  providedIn: 'root',
})
export class SubjectService {
  private http = inject(HttpClient);
  private subjectsUrl = 'http://localhost:8080/subjects';

  getSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(this.subjectsUrl);
  }

  addSubject(subject: Subject): Observable<Subject> {
    return this.http.post<Subject>(this.subjectsUrl, subject);
  }

  updateSubject(id: number, subject: Subject): Observable<Subject> {
    return this.http.put<Subject>(`${this.subjectsUrl}/${id}`, subject);
  }

  deleteSubject(id: number): Observable<any> {
    return this.http.delete<any>(`${this.subjectsUrl}/${id}`);
  }

  enrolUser(subjectId: number, userId: number): Observable<Subject> {
    return this.http.post<Subject>(`${this.subjectsUrl}/${subjectId}/users/${userId}`, {});
  }

  unenrolUser(subjectId: number, userId: number): Observable<Subject> {
    return this.http.delete<Subject>(`${this.subjectsUrl}/${subjectId}/users/${userId}`);
  }
}
