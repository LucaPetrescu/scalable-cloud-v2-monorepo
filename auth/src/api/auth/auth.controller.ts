import {
  Controller,
  Post,
  Logger,
  Request,
  UseGuards,
  Get,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { LocalAuthGuard } from './guards/local-auth.guard';

@Controller('auth')
export class AuthController {
  //----------------------------------------------------------------------
  //Service Fields
  //----------------------------------------------------------------------

  logger: Logger;

  //----------------------------------------------------------------------
  //Constructor
  //----------------------------------------------------------------------

  constructor(private readonly authService: AuthService) {
    this.logger = new Logger(AuthController.name);
  }

  //----------------------------------------------------------------------
  //Guard methods
  //----------------------------------------------------------------------

  @Post('login')
  @UseGuards(LocalAuthGuard)
  async login(@Request() req): Promise<any> {
    try {
      return await this.authService.generateJwtToken(req.user);
    } catch (error) {
      throw error;
    }
  }

  /********************************************************************************** */

  @UseGuards(JwtAuthGuard)
  @Get('viewProfile')
  async getUser(@Request() req): Promise<any> {
    return req.user;
  }
}
