import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';
import { Subject } from '../models/subject';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class SubjectService {
  private http = inject(HttpClient);
  private subjectsUrl = 'http://localhost:8080/subjects';

  getSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(this.subjectsUrl).pipe(
      tap((list: Subject[]) => console.log(`SubjectService: fetched ${list.length} subjects`)),
      catchError(this.handleError<Subject[]>('getSubjects', []))
    );
  }

  addSubject(subject: Subject): Observable<Subject> {
    return this.http.post<Subject>(this.subjectsUrl, subject, httpOptions).pipe(
      tap((added: Subject) => console.log(`SubjectService: added subject id=${added.id}`)),
      catchError(this.handleError<Subject>('addSubject'))
    );
  }

  updateSubject(id: number, subject: Subject): Observable<Subject> {
    return this.http.put<Subject>(`${this.subjectsUrl}/${id}`, subject, httpOptions).pipe(
      tap((updated: Subject) => console.log(`SubjectService: updated subject id=${updated.id}`)),
      catchError(this.handleError<Subject>('updateSubject'))
    );
  }

  deleteSubject(id: number): Observable<any> {
    return this.http.delete<any>(`${this.subjectsUrl}/${id}`, httpOptions).pipe(
      tap(() => console.log(`SubjectService: deleted subject id=${id}`)),
      catchError(this.handleError<any>('deleteSubject'))
    );
  }

  enrolUser(subjectId: number, userId: number): Observable<Subject> {
    return this.http.post<Subject>(`${this.subjectsUrl}/${subjectId}/users/${userId}`, {}, httpOptions).pipe(
      tap(() => console.log(`SubjectService: enrolled user ${userId} in subject ${subjectId}`)),
      catchError(this.handleError<Subject>('enrolUser'))
    );
  }

  unenrolUser(subjectId: number, userId: number): Observable<Subject> {
    return this.http.delete<Subject>(`${this.subjectsUrl}/${subjectId}/users/${userId}`, httpOptions).pipe(
      tap(() => console.log(`SubjectService: unenrolled user ${userId} from subject ${subjectId}`)),
      catchError(this.handleError<Subject>('unenrolUser'))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      return of(result as T);
    };
  }
}
