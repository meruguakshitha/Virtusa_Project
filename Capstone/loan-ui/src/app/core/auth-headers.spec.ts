import { HttpHeaders } from '@angular/common/http';
//import { authHeaders } from './auth-headers';
import { authHeaders } from './auth-header';
describe('authHeaders()', () => {

  afterEach(() => {
    localStorage.clear();
  });

  it('should return Authorization header when token exists', () => {
    localStorage.setItem('token', 'fake-jwt-token');

    const result = authHeaders();

    expect(result.headers instanceof HttpHeaders).toBeTrue();
    expect(result.headers.get('Authorization'))
      .toBe('Bearer fake-jwt-token');
  });

  it('should return Authorization header with null token when token does not exist', () => {
    localStorage.clear();

    const result = authHeaders();

    expect(result.headers instanceof HttpHeaders).toBeTrue();
    expect(result.headers.get('Authorization'))
      .toBe('Bearer null');
  });
});
