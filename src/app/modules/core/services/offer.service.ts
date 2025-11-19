import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Offer, OfferFilters, Page } from '../../homepage/models/offer.model';

@Injectable({
  providedIn: 'root'
})
export class OfferService {

  private apiUrl = `${environment.apiUrl}/api/oferty`;
  private ulubioneUrl = `${environment.apiUrl}/api/ulubione`;
  private http = inject(HttpClient);

  // Pobieranie listy z filtrami
  getOffers(page: number, size: number, filters?: OfferFilters): Observable<Page<Offer>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.nazwaMiasta) params = params.set('nazwaMiasta', filters.nazwaMiasta);
      if (filters.nazwaFirmy) params = params.set('nazwaFirmy', filters.nazwaFirmy);
      if (filters.kodWoj) params = params.set('kodWoj', filters.kodWoj);
      if (filters.widelkiMin) params = params.set('minWidelki', filters.widelkiMin.toString());
      if (filters.widelkiMax) params = params.set('maxWidelki', filters.widelkiMax.toString());
    }

    return this.http.get<Page<Offer>>(this.apiUrl, { params });
  }

  //  Pobieranie szczegółów oferty (do popupu)
  getOfferDetails(id: number): Observable<Offer> {
    return this.http.get<Offer>(`${this.apiUrl}/${id}`);
  }

  // Toggle Favorite
  toggleFavorite(offerId: number): Observable<boolean> {
    return this.http.post<boolean>(`${this.ulubioneUrl}/${offerId}/toggle`, {});
  }

  getFavoriteOffers(page: number, size: number): Observable<Page<Offer>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Offer>>(this.ulubioneUrl, { params });
  }

}
