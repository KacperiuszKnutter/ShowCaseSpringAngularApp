import { Injectable } from '@angular/core';
import { OfferFilters } from '../models/offer.model';

// eksportujemy to co istotne
export interface OffersState {
  pageIndex: number;
  pageSize: number;
  filters: OfferFilters;
}

@Injectable({
  providedIn: 'root'
})
export class OffersStateService {

  private initialState: OffersState = {
    pageIndex: 0,
    pageSize: 10,
    filters: {
      nazwaMiasta: '',
      nazwaFirmy: '',
      kodWoj: '',
      widelkiMin: 0,
      widelkiMax: 50000,
      krotkiOpis: '',
      technologie: []
    }
  };

  private state: OffersState = { ...this.initialState };

  // Metoda do pobrania stanu
  getState(): OffersState {
    return this.state;
  }

  // Metoda do aktualizacji stanu
  setState(newState: Partial<OffersState>): void {
    this.state = { ...this.state, ...newState };
  }

  // Metoda do resetowania stanu (np. przy wylogowaniu)
  clearState(): void {
    this.state = { ...this.initialState };
  }
}
