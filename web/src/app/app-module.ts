import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';
import { LucideAngularModule, 
  LayoutDashboard, Wallet, BarChart3, MoreHorizontal, Calendar, Bell, Settings, LogOut, Search,
  Building2, FileText, ArrowRightLeft, CreditCard, ChevronDown, Gift, Umbrella, Smartphone, Shield, Receipt, ArrowRight, Plus, BanknoteArrowDown,
  Eye, EyeOff, Lock, ShieldCheck, ArrowUp, ArrowDown, Coffee, DollarSign, Calculator, ArrowLeftRight, Loader2, AlertCircle, Wifi, WifiOff
} from 'lucide-angular';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './pages/login/login';
import { RegisterComponent } from './pages/register-component/register-component';

@NgModule({
  declarations: [
    App
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    Login
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor]))
  ],
  bootstrap: [App]
})
export class AppModule { }
