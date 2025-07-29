import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userId = 1;
  private role: string = '';
  private name: string = '';

  constructor(private http: HttpClient) {}

  loadUser(): Observable<any> {
    return this.http.get<any>(`/api/users/${this.userId}`).pipe(
      tap(user => {
        this.role = user.role.toUpperCase();
        this.name = user.name;
      })
    );
  }
  getUserById(userId: number): Observable<any> {
    return this.http.get<any>(`/api/users/${userId}`);
  }

  getAllLeaveTypesForUser(userId: number): Observable<any> {
    return this.http.get<any>(`/api/leavetypes/user/${userId}/leave_types`);
  }

  getUser(): number {
    return this.userId;
  }

  getRole(): string {
    return this.role;
  }

  getName(): string {
    return this.name;
  }

  setUserId(id: number) {
    this.userId = id;
  }
}
