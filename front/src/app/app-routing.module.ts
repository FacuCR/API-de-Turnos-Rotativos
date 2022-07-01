import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleAccesoGuard } from './core/guard/role-acceso.guard';
import { Role } from './core/models/Role';

const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./modules/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'jornada',
    loadChildren: () =>
      import('./modules/jornada/jornada.module').then((m) => m.JornadaModule),
    canActivate: [RoleAccesoGuard],
    data: { roles: [Role.Admin, Role.User] },
  },
  {
    path: 'registro',
    loadChildren: () =>
      import('./modules/sign-up/sign-up.module').then((m) => m.SignUpModule),
    canActivate: [RoleAccesoGuard],
    data: { roles: [Role.Admin] },
  },
  {
    path: 'perfil',
    loadChildren: () =>
      import('./modules/perfil/perfil.module').then((m) => m.PerfilModule),
    canActivate: [RoleAccesoGuard],
    data: { roles: [Role.Admin, Role.User] },
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
