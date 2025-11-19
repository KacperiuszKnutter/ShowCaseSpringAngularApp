import { Component, inject } from '@angular/core';
import { MatButton} from '@angular/material/button';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Offer } from '../../../homepage/models/offer.model';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [MatToolbar,
    MatIcon,
    MatButton,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatDialogModule],
  templateUrl: './popup.html',
  styleUrl: './popup.scss',
})
export class Popup {
  readonly data = inject<Offer>(MAT_DIALOG_DATA);
}
