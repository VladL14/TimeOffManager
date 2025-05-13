import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userId = 3;
  private role: string = '';
  private name: string = '';

  constructor(private http: HttpClient) {}

  loadUser() {
    this.http.get<any>(`/api/users/${this.userId}`).subscribe(user => {
      this.role=user.role.toUpperCase();
      this.name=user.name;
    });
  }
  getVacationBalance(userId: number): Observable<number> {
  return this.http.get<number>(`/api/leavetypes/user/${userId}/vacation`);
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

}
