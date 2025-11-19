import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { User, UserModel } from '../../homepage/models/user.model';
import { MatSnackBar } from '@angular/material/snack-bar';

// Interfejsy zgodne z Twoim backendem DTO
export interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  //obiekt uzytkownika ktory jest obecnie zalogowany
  userObj: User = {
    email: '',
    username: ''
  }


  private apiUrl = `${environment.apiUrl}/api/auth`;
  private http = inject(HttpClient);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar)

  // Klucz pod którym trzymamy token w przeglądarce
  private readonly TOKEN_KEY = 'auth_token';


  //do kontrolowania stanu zalogowania i dynamicznych zmian w header np.
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();


  login(userData: UserModel): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, userData)
      .pipe(
        // Operator 'tap' pozwala wykonać akcję "przy okazji" sukcesu (nie zmieniając danych)
        tap(response => {
          if (response.token) {
            //Zapisujemy token w pamięci przeglądarki
            localStorage.setItem(this.TOKEN_KEY, response.token);
            this.isLoggedInSubject.next(true);
            this.userObj.username = userData.email.substring(userData.email.indexOf("@" ) + 1);
            this.userObj.email = userData.email;
          }
        })
      );
  }


  register(userData: UserModel): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/rejestracja`, userData)
      .pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem(this.TOKEN_KEY, response.token);
            this.isLoggedInSubject.next(true);
            this.userObj.username = userData.email.substring(userData.email.indexOf("@" ) + 1);
            this.userObj.email = userData.email;
          }
        })
      );
  }


  logout(): void {
    // Usuwamy token
    localStorage.removeItem(this.TOKEN_KEY);
    // Przekierowujemy na stronę główną
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/']);
    this.snackBar.open('Wylogowano pomyślnie!', 'OK', { duration: 3000 });
    this.resetUserInstance();
  }

  // helpers

  // Pobiera token (potrzebne do interceptora)
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  // Sprawdza czy użytkownik jest zalogowany
  isLoggedIn(): boolean {
    return !!this.getToken(); // Zwraca true jeśli token istnieje
  }

  private resetUserInstance(){
    this.userObj.email = '';
    this.userObj.username = '';
  }
}
