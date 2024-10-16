import {
  Controller,
  Post,
  Logger,
  Request,
  UseGuards,
  Get,
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
  async login(@Request() req): Promise<any> {
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
      return await this.authService.generateJwtToken(req.user);
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
