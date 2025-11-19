import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatError, MatLabel } from '@angular/material/form-field';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { User, UserModel } from '../../../homepage/models/user.model';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card'; //
import { Router, RouterLink } from '@angular/router'; // Do linku rejestracji
import {
  MatCard,
  MatCardContent,
  MatCardFooter,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import { AuthService } from '../../services/auth';
import { MatSnackBar } from '@angular/material/snack-bar';



@Component({
  selector: 'app-login-form',
  imports: [
    FormsModule,
    MatLabel,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatButton,
    MatCardHeader,
    MatCard,
    MatCardSubtitle,
    MatCardTitle,
    MatCardContent,
    MatError,
    MatCardFooter,
    RouterLink
  ],
  templateUrl: './login.form.html',
  styleUrl: './login.form.scss',
})
export class LoginForm {

  isLoading = false; // Do blokowania przycisku podczas requestu

  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  userData: UserModel ={
    email: '',
    password: ''
  }


  hide = signal(true);

  onLogin() {
    if(!this.userData.email || !this.userData.password)return;
    this.isLoading = true;

    this.authService.login(this.userData).subscribe({
      next: (response) => {
        console.log('Zalogowano pomyślnie!', response);
        this.isLoading = false;

        // Wyświetl komunikat sukcesu
        this.snackBar.open('Zalogowano pomyślnie!', 'OK', { duration: 3000 });
        // Przekieruj na stronę główną
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Błąd logowania:', err);
        this.isLoading = false;

        // Obsługa błędów (np. 403 Forbidden)
        let message = 'Błąd logowania. Sprawdź dane.';
        if (err.status === 403 || err.status === 401) {
          message = 'Nieprawidłowy email lub hasło.';
        }
        this.snackBar.open(message, 'Zamknij', { duration: 5000, panelClass: ['error-snackbar'] });
      }
    });
  }

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
    event.preventDefault();
  }
}
