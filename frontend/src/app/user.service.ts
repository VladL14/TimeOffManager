import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private user = 2;

  getUser(): number {
    return this.user;
  }

}
