import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { PixKeysComponent } from '../../components/pix-keys/pix-keys';

@Component({
  selector: 'app-pix-keys-page',
  standalone: true,
  imports: [CommonModule, RouterModule, Layout, PixKeysComponent],
  templateUrl: './pix-keys-page.html',
  styleUrl: './pix-keys-page.css'
})
export class PixKeysPage {}


