import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Role } from 'src/app/core/models/Role';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  isAdmin: boolean = false;
  constructor(
    private tokenStorage: TokenStorageService,
    private route: Router
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.tokenStorage.getUser().roles.some((rol: string) => rol === "ROLE_ADMIN");
  }

  onLogoutClick(): void {
    this.tokenStorage.signOut();
  }
}
