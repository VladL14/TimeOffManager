import { Component } from '@angular/core';
import { RoleService, Role } from '../role.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-menu',
  imports: [],
  templateUrl: './main-menu.component.html',
  styleUrl: './main-menu.component.scss'
})
export class MainMenuComponent {
  role: Role;

  constructor(private roleService: RoleService, private router: Router) {
    this.role = this.roleService.getRole();
  }
  goToDashboard() {
    const role = this.roleService.getRole().toLowerCase();
    this.router.navigate([`/dashboard/${role}`]);
  }
  
}
