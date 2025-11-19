import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth';

// interceptor obsluguje operacje logowania i wylogwywania oraz przechowuje token
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Jeśli mamy token, klonujemy zapytanie i dodajemy nagłówek
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // format naglowka dla jwt
      }
    });
    return next(authReq);
  }

  // Jeśli nie ma tokena, puszczamy zapytanie bez zmian
  return next(req);
};
