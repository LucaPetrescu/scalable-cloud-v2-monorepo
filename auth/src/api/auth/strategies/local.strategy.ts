import { Strategy } from 'passport-local';
import { PassportStrategy } from '@nestjs/passport';
import { AuthService } from '../auth.service';
import { Injectable, UnauthorizedException, Logger } from '@nestjs/common';

@Injectable()
export class LocalStrategy extends PassportStrategy(Strategy) {
  //----------------------------------------------------------------------
  //Service Fields
  //----------------------------------------------------------------------

  logger: Logger;

  //----------------------------------------------------------------------
  //Constructor
  //----------------------------------------------------------------------

  constructor(private authService: AuthService) {
    super({ usernameField: 'email' });
    this.logger = new Logger(LocalStrategy.name);
  }

  //----------------------------------------------------------------------
  //Methods
  //----------------------------------------------------------------------

  async validate(email: string, password: string): Promise<any> {
    const user = await this.authService.validateUser(email, password);

    if (!user) {
      throw new UnauthorizedException();
    }

    return user;
  }
}
