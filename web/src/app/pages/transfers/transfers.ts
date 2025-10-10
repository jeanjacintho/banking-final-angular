import { Component } from '@angular/core';
import { TransfersComponent } from '../../components/transfers/transfers';

@Component({
  selector: 'app-transfers-page',
  standalone: true,
  imports: [TransfersComponent],
  template: `
    <div class="transfers-page">
      <app-transfers></app-transfers>
    </div>
  `,
  styles: [`
    .transfers-page {
      min-height: 100vh;
      background: var(--bg-primary);
    }
  `]
})
export class TransfersPageComponent {
}
