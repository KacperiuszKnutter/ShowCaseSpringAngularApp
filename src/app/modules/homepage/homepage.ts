import { Component, OnInit, ViewChild,  inject, } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { OfferService } from '../core/services/offer.service';
import { Offer, OfferFilters } from './models/offer.model';
import { OffersStateService } from './services/offers.state.service';
import { MatDialog } from '@angular/material/dialog';
import {Popup} from '../core/components/popup/popup';
import { AuthService } from '../core/services/auth';
import {User} from './models/user.model';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatCardModule,
    MatChipsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSliderModule,
    MatIconModule,
    MatSelectModule
  ],
  templateUrl: './homepage.html',
  styleUrl: './homepage.scss',
})
export class HomepageComponent implements OnInit {
  displayedColumns: string[] = ['stanowisko', 'firma', 'miasto', 'wynagrodzenie', 'akcje'];
  dataSource: Offer[] = [];
  totalElements = 0;
  isLoading = false;

  //  Pobieramy stan początkowy z serwisu (lub domyślny, jeśli pusty)
  private stateService = inject(OffersStateService);
  private savedState = this.stateService.getState();
  private route = inject(ActivatedRoute);

  //  Inicjujemy zmienne wartościami z Cache'a
  pageSize = this.savedState.pageSize;
  pageIndex = this.savedState.pageIndex;

  // wstrzykujemy stan uzytkownika z serwisu autoryzacji by zmieniac widok w tym komponencie
  private authService = inject(AuthService);
  isLoggedIn$ = this.authService.isLoggedIn$;
  userObj$ = this.authService.userObj;
  private offersService = inject(OfferService);
  filterForm: FormGroup;
  isFavoritesMode = false;


  // Lista województw (do selecta)
  wojewodztwa = [
    { kod: 'DS', nazwa: 'Dolnośląskie' },
    { kod: 'KP', nazwa: 'Kujawsko-pomorskie' },
    { kod: 'LU', nazwa: 'Lubelskie' },
    { kod: 'LB', nazwa: 'Lubuskie' },
    { kod: 'LD', nazwa: 'Łódzkie' },
    { kod: 'MA', nazwa: 'Małopolskie' },
    { kod: 'MZ', nazwa: 'Mazowieckie' },
    { kod: 'OP', nazwa: 'Opolskie' },
    { kod: 'PK', nazwa: 'Podkarpackie' },
    { kod: 'PL', nazwa: 'Podlaskie' },
    { kod: 'PM', nazwa: 'Pomorskie' },
    { kod: 'SL', nazwa: 'Śląskie' },
    { kod: 'SK', nazwa: 'Świętokrzyskie' },
    { kod: 'WN', nazwa: 'Warmińsko-mazurskie' },
    { kod: 'WP', nazwa: 'Wielkopolskie' },
    { kod: 'ZP', nazwa: 'Zachodniopomorskie' }
  ];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private fb = inject(FormBuilder);

  constructor() {
    // ✅ Wypełniamy formularz wartościami z Cache'a
    const savedFilters = this.savedState.filters;

    this.filterForm = this.fb.group({
      nazwaMiasta: [savedFilters.nazwaMiasta || ''],
      nazwaFirmy: [savedFilters.nazwaFirmy || ''],
      kodWoj: [savedFilters.kodWoj || ''],
      widelkiMin: [savedFilters.widelkiMin || 0],
      widelkiMax: [savedFilters.widelkiMax || 50000]
    });
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.isFavoritesMode = data['mode'] === 'favorites';

      // Jeśli weszliśmy w ulubione, resetujemy stronę na 0 (żeby nie szukać np. 5 strony ulubionych, gdy mamy ich mało)
      if (this.isFavoritesMode) {
        this.pageIndex = 0;
      }

      this.loadOffers();
    });
  }

  applyFilters(): void {
    this.pageIndex = 0;
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadOffers();
  }

  clearFilters(): void {
    this.filterForm.reset({
      widelkiMin: 0,
      widelkiMax: 50000,
      kodWoj: '' // Reset selecta
    });

    // Resetujemy też stan w serwisie
    this.stateService.clearState();

    this.applyFilters();
  }

  loadOffers(): void {
    this.isLoading = true;

    if (this.isFavoritesMode) {
      // W trybie ulubionych ignorujemy filtry wyszukiwania (dla uproszczenia)
      // albo możemy je obsłużyć, jeśli backend ma odpowiedni endpoint.
      // Tutaj pobieramy po prostu listę ulubionych z paginacją.
      this.offersService.getFavoriteOffers(this.pageIndex, this.pageSize).subscribe({
        next: (response) => this.handleResponse(response),
        error: (err) => this.handleError(err)
      });
    }
    // 2. TRYB WYSZUKIWANIA (ZWYKŁY)
    else {

      const formValues = this.filterForm.value;

      const filters: OfferFilters = {};
      // Mapowanie formularza na obiekt filtrów
      if (formValues.nazwaMiasta) filters.nazwaMiasta = formValues.nazwaMiasta;
      if (formValues.nazwaFirmy) filters.nazwaFirmy = formValues.nazwaFirmy;
      if (formValues.kodWoj) filters.kodWoj = formValues.kodWoj;
      if (formValues.widelkiMin > 0) filters.widelkiMin = formValues.widelkiMin;
      if (formValues.widelkiMax < 50000) filters.widelkiMax = formValues.widelkiMax;

      // cachowanie
      // Za każdym razem, gdy ładujemy dane, aktualizujemy "pamięć" serwisu
      this.stateService.setState({
        pageIndex: this.pageIndex,
        pageSize: this.pageSize,
        // Musimy zapisać też surowe wartości z formularza, żeby po powrocie
        // pola były wypełnione (nawet te puste, żeby nadpisać stare)
        filters: {
          nazwaMiasta: formValues.nazwaMiasta,
          nazwaFirmy: formValues.nazwaFirmy,
          kodWoj: formValues.kodWoj,
          widelkiMin: formValues.widelkiMin,
          widelkiMax: formValues.widelkiMax,
          krotkiOpis: filters.krotkiOpis,
          technologie: filters.technologie
        }
      });

      this.offersService.getOffers(this.pageIndex, this.pageSize, filters).subscribe({
        next: (response: any) => {
          this.handleResponse(response)
        },
        error: (err) => {
          this.handleError(err)
        }
      });
    }
  }


  private handleResponse(response: any) {
    this.dataSource = response.content;
    if (response.totalElements !== undefined) {
      this.totalElements = response.totalElements;
    } else if (response.page?.totalElements !== undefined) {
      this.totalElements = response.page.totalElements;
    }
    this.isLoading = false;
  }

  private handleError(err: any) {
    console.error(err);
    this.isLoading = false;
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadOffers();
  }

  formatLabel(value: number): string {
    if (value >= 1000) {
      return Math.round(value / 1000) + 'k';
    }
    return `${value}`;
  }

  // do okna dialogowego

  readonly dialog = inject(MatDialog)
  openDialog(partialOffer: Offer): void {
    this.offersService.getOfferDetails(partialOffer.id).subscribe({
      next: (fullOffer) => {
        this.dialog.open(Popup, {
          data: fullOffer,
          width: '600px',
          maxWidth: '90vw'
        });
      },
      error: (err) => console.error("Błąd pobierania szczegółów", err)
    });
  }
  toggleFavorite(offer: Offer, event: Event): void {
    event.stopPropagation();

    if (!this.authService.isLoggedIn()) {
      alert("Zaloguj się, aby dodawać do ulubionych!"); // Możesz użyć SnackBara
      return;
    }

    this.offersService.toggleFavorite(offer.id).subscribe({
      next: (isLikedNow) => {
        offer.isLiked = isLikedNow;
      },
      error: (err) => console.error(err)
    });
  }

}
