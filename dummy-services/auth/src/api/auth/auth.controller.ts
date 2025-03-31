import {
  Controller,
  Post,
  Logger,
  Request,
  UseGuards,
  HttpStatus,
  Get,
  Res,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { HttpMetricsService } from '../metrics/http-metrics.service';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { LocalAuthGuard } from './guards/local-auth.guard';

@Controller('auth')
export class AuthController {
  logger: Logger;

  constructor(
    private readonly authService: AuthService,
    private readonly httpMetricsService: HttpMetricsService,
  ) {
    this.logger = new Logger(AuthController.name);
  }

  @Post('login')
  @UseGuards(LocalAuthGuard)
  async login(@Request() req, @Res() res): Promise<any> {
    try {
      const token = await this.authService.generateJwtToken(req.user);

      res
        .status(HttpStatus.ACCEPTED)
        .send({ message: 'User authenticated', accessToken: token });
    } catch (error) {
      res
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .send({ message: 'Something went wrong' });
    }
  }

  @UseGuards(JwtAuthGuard)
  @Get('viewProfile')
  async getUser(@Request() req, @Res() res): Promise<any> {
    try {
      console.log("Hello world")
      res
        .status(HttpStatus.ACCEPTED)
        .send({ message: 'User returned successfully', user: req.user });
    } catch (error) {
      res
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .send({ message: 'Something went wrong' });
    }
  }
}
