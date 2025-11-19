import { Routes } from '@angular/router';
import { HomepageComponent } from './modules/homepage/homepage';
import { LoginForm } from './modules/core/components/login.form/login.form';
import { RegisterForm } from './modules/core/components/register.form/register.form';

export const routes: Routes = [

  { path: '', component: HomepageComponent, pathMatch: 'full' },

   { path: 'logowanie', component: LoginForm, pathMatch: 'full' },
  {path: 'rejestracja' , component:RegisterForm, pathMatch:'full'},
  {
    path: 'ulubione',
    component: HomepageComponent,
    data: { mode: 'favorites' }
  },

  //  przekierowanie nieznanych adresów na stronę główną
  { path: '**', redirectTo: '' }
];
