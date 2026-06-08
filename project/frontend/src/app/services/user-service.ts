import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';
import { User } from '../models/user';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private usersUrl = 'http://localhost:8080/users';

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl, httpOptions).pipe(
      tap((list: User[]) => console.log(`UserService: fetched ${list.length} users`)),
      catchError(this.handleError<User[]>('getUsers', []))
    );
  }

  updateUserRole(userId: number, role: string): Observable<User> {
    let params = new HttpParams().set('role', role);
    return this.http.put<User>(`${this.usersUrl}/${userId}/role`, {}, { ...httpOptions, params }).pipe(
      tap((updated: User) => console.log(`UserService: updated user role, id=${updated.id}`)),
      catchError(this.handleError<User>('updateUserRole'))
    );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      return of(result as T);
    };
  }
}
