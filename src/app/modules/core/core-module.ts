import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Header} from './components/header/header';

// NVM DEPRECATED JEST NIE BEDZIEMY KORZYSTAC Z CORE MODULE tylko standalone components, w nowszej wersji Angular to tamto juz jest niedozwolone i guess np. shared imports miedzy modulami
@NgModule({
  declarations: [

  ],
  imports: [
    CommonModule,
    Header
  ]
})
export class CoreModule { }
