import { inject, Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Grade } from "../models/grade";

export interface GradeRequest {
  value: number;
  description: string;
  subjectId: number;
  studentId: number;
}

@Injectable({
  providedIn: "root",
})
export class GradeService {
  private http = inject(HttpClient);
  private gradesUrl = "http://localhost:8080/grades";

  getGrades(studentId?: number): Observable<Grade[]> {
    let params = {};
    if (studentId !== undefined) {
      params = { studentId: studentId.toString() };
    }
    return this.http.get<Grade[]>(this.gradesUrl, { params });
  }

  addGrade(gradeRequest: GradeRequest): Observable<Grade> {
    return this.http.post<Grade>(this.gradesUrl, gradeRequest);
  }

  updateGrade(id: number, gradeRequest: GradeRequest): Observable<Grade> {
    return this.http.put<Grade>(`${this.gradesUrl}/${id}`, gradeRequest);
  }
}
