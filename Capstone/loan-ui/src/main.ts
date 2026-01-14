import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { AuthInterceptor } from './app/shared/interceptors/auth.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),                // ✅ REQUIRED
    provideHttpClient(withInterceptorsFromDi()), // ✅ REQUIRED
    {
      provide: HTTP_INTERCEPTORS,         // ✅ REQUIRED
      useClass: AuthInterceptor,
      multi: true
    }
  ]
});
