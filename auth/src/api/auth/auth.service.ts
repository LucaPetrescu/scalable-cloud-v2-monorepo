import {
  Logger,
  UnauthorizedException,
  Inject,
  forwardRef,
} from '@nestjs/common';
import { UserService } from '../user/user.service';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';

export class AuthService {
  //----------------------------------------------------------------------
  //Service Fields
  //----------------------------------------------------------------------

  logger: Logger;

  //----------------------------------------------------------------------
  //Constructor
  //----------------------------------------------------------------------

  constructor(
    @Inject(forwardRef(() => UserService))
    private userService: UserService,
    private jwtService: JwtService,
  ) {}

  //----------------------------------------------------------------------
  //Validating methods
  //----------------------------------------------------------------------

  async validateUser(email: string, pass: string): Promise<any> {
    const query = { email: email };
    const user = await this.userService.findOne(query);

    if (!user) {
      throw new UnauthorizedException('Wrong Email or Password!');
    }

    const isMatched = await this.comparePasswords(pass, user.password);

    if (!isMatched) {
      throw new UnauthorizedException('Wrong Email or Password!');
    }

    return user;
  }

  /********************************************************************************** */

  async generateJwtToken(user: any): Promise<String> {
    const payload = {
      email: user.email,
    };

    const accessToken = this.jwtService.sign(payload);
    return accessToken;
  }

  /********************************************************************************** */

  async getHashedPassword(plainTextPassword: any): Promise<String> {
    const hashedPassword = await bcrypt.hash(plainTextPassword, 10);
    return hashedPassword;
  }

  /********************************************************************************** */

  async comparePasswords(
    plainTextPassword: string,
    hashedPassword: string,
  ): Promise<Boolean> {
    return await bcrypt.compare(plainTextPassword, hashedPassword);
  }
}
