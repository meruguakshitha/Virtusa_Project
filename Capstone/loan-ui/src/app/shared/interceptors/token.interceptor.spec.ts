import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { tokenInterceptor } from './token.interceptor';

describe('tokenInterceptor', () => {
  let http: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([tokenInterceptor]))
      ]
    });

    http = TestBed.inject(HttpClient);
  });

  it('should add Authorization header when token exists', () => {
    localStorage.setItem('token', 'fake-token');

    spyOn(http, 'get').and.callFake((url: any, options: any) => {
      expect(options?.headers?.get('Authorization'))
        .toBe('Bearer fake-token');
      return {} as any;
    });

    http.get('/api/test');
  });

  it('should not add Authorization header when token does not exist', () => {
    localStorage.clear();

    spyOn(http, 'get').and.callFake((url: any, options: any) => {
      expect(options?.headers?.has('Authorization')).toBeFalse();
      return {} as any;
    });

    http.get('/api/test');
  });
});
