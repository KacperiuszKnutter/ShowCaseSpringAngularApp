import { Component, inject } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { filter, map, Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    MatIcon,
    MatButton,
    RouterLink,
    CommonModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {

  private authService = inject(AuthService);
  private router = inject(Router);
  isLoggedIn$ = this.authService.isLoggedIn$;

  isFavPage$: Observable<boolean>;

  logout() : void {
    this.authService.logout();
  }

  constructor() {
    const navEnd$ = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ) as Observable<NavigationEnd>;

    // Mapujemy zdarzenie na boolean (czy url to '/ulubione')
    this.isFavPage$ = navEnd$.pipe(
      map(event => event.urlAfterRedirects.includes('/ulubione'))
    );
  }
}
