import { TestBed } from '@angular/core/testing';

import { OffersStateService } from './offers.state.service';

describe('OffersStateService', () => {
  let service: OffersStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OffersStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
