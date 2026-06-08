import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';
import { Grade } from '../models/grade';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

export interface GradeRequest {
  value: number;
  description: string;
  subjectId: number;
  studentId: number;
}

@Injectable({
  providedIn: 'root',
})
export class GradeService {
  private http = inject(HttpClient);
  private gradesUrl = 'http://localhost:8080/grades';

  getGrades(studentId?: number): Observable<Grade[]> {
    let params = new HttpParams();
    if (studentId !== undefined) {
      params = params.set('studentId', studentId.toString());
    }
    return this.http.get<Grade[]>(this.gradesUrl, { ...httpOptions, params }).pipe(
      tap((list: Grade[]) => console.log(`GradeService: fetched ${list.length} grades`)),
      catchError(this.handleError<Grade[]>('getGrades', []))
    );
  }

  addGrade(gradeRequest: GradeRequest): Observable<Grade> {
    return this.http.post<Grade>(this.gradesUrl, gradeRequest, httpOptions).pipe(
      tap((added: Grade) => console.log(`GradeService: added grade id=${added.id}`)),
      catchError(this.handleError<Grade>('addGrade'))
    );
  }

  updateGrade(id: number, gradeRequest: GradeRequest): Observable<Grade> {
    return this.http.put<Grade>(`${this.gradesUrl}/${id}`, gradeRequest, httpOptions).pipe(
      tap((updated: Grade) => console.log(`GradeService: updated grade id=${updated.id}`)),
      catchError(this.handleError<Grade>('updateGrade'))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      return of(result as T);
    };
  }
}
