import { bootstrapApplication, platformBrowser } from '@angular/platform-browser';
import { routes } from './app/app-routing-module';
import { AppComponent } from './app/app';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
bootstrapApplication(AppComponent, {
    providers: [
        provideRouter(routes),
        provideHttpClient()
    ]
}).catch(err => console.error(err));
