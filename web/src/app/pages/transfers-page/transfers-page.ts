import { Component } from '@angular/core';
import { Layout } from '../../components/layout/layout';
import { TransfersComponent } from '../../components/transfers/transfers';
import { TransferHistoryComponent } from '../../components/transfer-history/transfer-history';

@Component({
  selector: 'app-transfers-page',
  imports: [Layout, TransfersComponent, TransferHistoryComponent],
  templateUrl: './transfers-page.html',
  styleUrls: ['./transfers-page.css']
})
export class TransfersPage {

}
