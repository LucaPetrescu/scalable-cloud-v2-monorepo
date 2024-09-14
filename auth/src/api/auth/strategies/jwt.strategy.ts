import { Injectable, Logger, Inject, forwardRef } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { UserService } from 'src/api/user/user.service';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  //----------------------------------------------------------------------
  //Service Fields
  //----------------------------------------------------------------------

  logger: Logger;

  //----------------------------------------------------------------------
  //Constructor
  //----------------------------------------------------------------------

  constructor(
    @Inject(forwardRef(() => UserService))
    private readonly UserService: UserService,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: 'JWT_SECRET',
    });
    this.logger = new Logger(JwtStrategy.name);
  }

  //----------------------------------------------------------------------
  //Methods
  //----------------------------------------------------------------------

  async validate(payload: any) {
    return await this.UserService.findOne({ email: payload.email });
  }
}
