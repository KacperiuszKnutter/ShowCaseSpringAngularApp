//odzwierciedlenie obiektu DTO z naszego backendu
export interface Offer {
  id: number;
  nazwaStanowiska: string;
  widelkiMin: number;
  widelkiMax: number;
  nazwaFirmy: string;
  nazwaMiasta: string;
  krotkiOpis: string;
  technologie: string[];
  isLiked?: boolean;
}

// To odzwierciedla obiekt Page<T> ze Springa
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // Aktualny numer strony (liczony od 0)
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// filtry ofert potrzebne do serwisu z filtrowaniem naszych ofert pracy
export interface OfferFilters {
  nazwaMiasta?: string;
  nazwaFirmy?: string;
  kodWoj?: string;
  widelkiMin?: number;
  widelkiMax?: number;
  krotkiOpis?: string;
  technologie?: string[];

}

