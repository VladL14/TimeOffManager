import { Component } from '@angular/core';
import { RoleService, Role } from '../role.service';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-main-menu',
  imports: [RouterModule,NgIf],
  templateUrl: './main-menu.component.html',
  styleUrl: './main-menu.component.scss'
})
export class MainMenuComponent {
  role: Role;
  showDashboard = false;


  constructor(private roleService: RoleService, private router: Router) {
    this.role = this.roleService.getRole();
  }
  goToDashboard() {
    this.showDashboard = true;
    const role = this.roleService.getRole().toLowerCase();
    this.router.navigate([`/dashboard/${role}`]);
  }
  
}
