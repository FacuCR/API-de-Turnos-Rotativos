import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { TokenStorageService } from '../services/token/token-storage.service';
import { Role } from '../models/Role';

@Injectable({
  providedIn: 'root',
})
export class RoleAccesoGuard implements CanActivate {
  constructor(
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    const user = this.tokenStorage.getUser();
    
    // Reviso si la ruta esta restringida por roles
    if (user.roles) {
      // si tiene algun role que cumpla la condicion devuelve true
      let algo: boolean = user.roles.some((rol: Role) => {return route.data['roles'].indexOf(rol) !== -1});

      if(algo) { // autorizado asi que retorno true
        return true;
      } else { // role no autorizado asi que redirecciono al home page
        this.router.navigate(['/']);
        return false;
      }
    }
      

    // no logeado asi que redirecciono al login page con el return url
    this.router.navigate(['/'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
