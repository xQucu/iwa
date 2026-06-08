import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private usersUrl = 'http://localhost:8080/users';

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl);
  }

  updateUserRole(userId: number, role: string): Observable<User> {
    return this.http.put<User>(`${this.usersUrl}/${userId}/role`, {}, { params: { role } });
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete<any>(`${this.usersUrl}/${id}`);
  }
}
