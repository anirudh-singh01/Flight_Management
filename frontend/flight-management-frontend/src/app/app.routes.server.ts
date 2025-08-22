import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'login',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'register',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'admin-register',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'carrier-register',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'flight-register',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'view-flights',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'update-flight/:id',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'update-carrier/:id',
    renderMode: RenderMode.Prerender
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
