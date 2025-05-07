import { Component } from '@angular/core';
import { SidebarLayoutComponent } from '../../layout/sidebar-layout/sidebar-layout.component';

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [SidebarLayoutComponent],
  templateUrl: './marketplace.component.html',
  styleUrl: './marketplace.component.css',
})
export class MarketplaceComponent {}
