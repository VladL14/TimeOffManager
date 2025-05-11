import { Injectable } from '@angular/core';
export type Role = 'ADMIN' | 'MANAGER' | 'USER';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private user = 2;

  getUser(): number {
    return this.user;
  }

  getRole(): Role {
    switch (this.user) {
      case 1: return 'ADMIN';
      case 2: return 'USER';
      case 3: return 'MANAGER';
      default: return 'USER';
    }
  }

}
