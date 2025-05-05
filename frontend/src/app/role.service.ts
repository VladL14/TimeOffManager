import { Injectable } from '@angular/core';
export type Role = 'ADMIN' | 'USER' | 'MANAGER';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private role: Role = 'USER'; // Default role

  getRole(): Role {
    return this.role;
  }
  setRole(role: Role): void {
    this.role = role;
  }
}
