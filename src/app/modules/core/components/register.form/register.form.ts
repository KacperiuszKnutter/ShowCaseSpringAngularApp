import { Component, inject, signal } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserModel } from '../../../homepage/models/user.model';

@Component({
  selector: 'app-register-form', // Poprawiłem selector na standardowy (kebab-case)
  standalone: true,
  imports: [
    ReactiveFormsModule, // ✅ Zmiana na ReactiveFormsModule
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './register.form.html',
  styleUrl: './register.form.scss',
})
export class RegisterForm {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  isLoading = false;
  hidePassword = signal(true);
  hideConfirmPassword = signal(true);

  //  Definicja Formularza z Walidatorami
  registerForm: FormGroup = this.fb.group({
    email: ['', [
      Validators.required,
      Validators.email,
      Validators.minLength(4),
      Validators.maxLength(50)
    ]],
    password: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(16),
      // Regex: co najmniej jedna mała litera, jedna duża litera, jedna cyfra
      Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/)
    ]],
    confirmPassword: ['', [Validators.required]]
  }, {
    //  Walidator na poziomie całej grupy (porównuje dwa pola)
    validators: this.passwordMatchValidator
  });

  // Niestandardowy walidator sprawdzający zgodność haseł
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    // Zwracamy błąd 'mismatch' jeśli hasła są różne
    return password === confirmPassword ? null : { mismatch: true };
  }

  onRegister() {
    if (this.registerForm.invalid) return;

    this.isLoading = true;

    // Przygotuj obiekt do wysłania (bez confirmPassword)
    const userData: UserModel = {
      email: this.registerForm.value.email,
      password: this.registerForm.value.password
    };

    this.authService.register(userData).subscribe({
      next: (response) => {
        console.log('Zarejestrowano pomyślnie!', response);
        this.isLoading = false;
        this.snackBar.open('Zarejestrowano pomyślnie!', 'OK', { duration: 3000 });
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Błąd rejestracji:', err);
        this.isLoading = false;
        let message = 'Błąd rejestracji!';
        if (err.status === 409) { // Conflict
          message = 'Ten email jest już zajęty.';
        } else if (err.status === 403 || err.status === 401) {
          message = 'Brak uprawnień.';
        }
        this.snackBar.open(message, 'Zamknij', { duration: 5000, panelClass: ['error-snackbar'] });
      }
    });
  }

  // Gettery dla łatwiejszego dostępu w HTML
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get confirmPassword() { return this.registerForm.get('confirmPassword'); }

  togglePassword(event: MouseEvent) {
    this.hidePassword.set(!this.hidePassword());
    event.stopPropagation();
    event.preventDefault();
  }

  toggleConfirmPassword(event: MouseEvent) {
    this.hideConfirmPassword.set(!this.hideConfirmPassword());
    event.stopPropagation();
    event.preventDefault();
  }
}
