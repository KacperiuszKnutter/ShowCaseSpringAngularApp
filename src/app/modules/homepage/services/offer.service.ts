import { Injectable, inject } from '@angular/core'; // 1. Importujemy inject
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Offer, OfferFilters, Page } from '../models/offer.model';



@Injectable({
  providedIn: 'root'
})
export class OfferService {

  private apiUrl = `${environment.apiUrl}/api/oferty`;

  // wstrzykiwanie zależności zamiast konstruktora
  private http = inject(HttpClient);

  // Metoda pobierająca oferty z paginacją i filtrami
  getOffers(page: number, size: number, filters?: OfferFilters): Observable<Page<Offer>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.nazwaMiasta) params = params.set('nazwaMiasta', filters.nazwaMiasta);
      if (filters.nazwaFirmy) params = params.set('nazwaFirmy', filters.nazwaFirmy);
      if (filters.kodWoj) params = params.set('kodWoj', filters.kodWoj);
      // Dodatkowe zabezpieczenie dla liczb (konwersja na string)
      if (filters.widelkiMin) params = params.set('widelkiMin', filters.widelkiMin.toString());
      if (filters.widelkiMax) params = params.set('widelkiMax', filters.widelkiMax.toString());
      if (filters.krotkiOpis) params = params.set('krotkiOpis', filters.krotkiOpis);
      if (filters.technologie) params = params.set('technologie', filters.technologie.join());
      if (filters.krotkiOpis) params = params.set('krotkiOpis', filters.krotkiOpis);

      // Sprawdzamy czy technologie istnieją i czy tablica nie jest pusta
      if (filters.technologie && filters.technologie.length > 0) {
        params = params.set('technologie', filters.technologie.join(','));
      }
    }

    return this.http.get<Page<Offer>>(this.apiUrl, { params });
  }
}
