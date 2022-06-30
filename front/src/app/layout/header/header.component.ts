import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from 'src/app/core/services/token/token-storage.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  constructor(
    private tokenStorage: TokenStorageService,
    private route: Router
  ) {}

  ngOnInit(): void {}

  onLogoutClick(): void {
    this.tokenStorage.signOut();
    this.route.navigate(['/']);
  }
}
