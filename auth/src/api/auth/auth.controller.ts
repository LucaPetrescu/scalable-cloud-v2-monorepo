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
    const startTime = Date.now();
    const durationInSeconds = (Date.now() - startTime) / 1000;
    const { method, path: route } = req;
    try {
      this.httpMetricsService.incrementRequestCounter(
        method,
        route,
        200,
        durationInSeconds,
      );

      const token = await this.authService.generateJwtToken(req.user);

      res
        .status(HttpStatus.ACCEPTED)
        .send({ message: 'User authenticated', accessToken: token });

      return token;
    } catch (error) {
      throw error;
    }
  }

  @UseGuards(JwtAuthGuard)
  @Get('viewProfile')
  async getUser(@Request() req): Promise<any> {
    const startTime = Date.now();
    const durationInSeconds = (Date.now() - startTime) / 1000;
    const { method, path: route } = req;
    this.httpMetricsService.incrementRequestCounter(
      method,
      route,
      200,
      durationInSeconds,
    );
    return req.user;
  }
}
