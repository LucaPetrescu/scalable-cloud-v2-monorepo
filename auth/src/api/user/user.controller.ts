/* eslint-disable */

import {
  Controller,
  Post,
  Request,
  Logger,
  ConflictException,
  HttpException,
  HttpStatus,
  Res,
} from '@nestjs/common';
import { HttpMetricsService } from '../metrics/http-metrics.service';
import { UserService } from './user.service';

@Controller('user')
export class UserController {
  logger: Logger;

  constructor(
    private readonly userService: UserService,
    private readonly httpMetricsService: HttpMetricsService,
  ) {
    this.logger = new Logger('UserController');
  }

  @Post('create')
  async create(@Request() req, @Res() res): Promise<any> {
    const newUser = req.body;
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
      const query = { email: newUser.email };
      const isUser = await this.userService.findOne(query);
      if (isUser) {
        throw new HttpException('Email already exists', HttpStatus.CONFLICT);
      }
      const user = await this.userService.create(newUser);
      console.log(user);
      res.status(HttpStatus.CREATED).send('User Created');

      return user;
    } catch (err) {
      this.logger.error('Something went wrong. Please try again later:', err);
      res.status(HttpStatus.INTERNAL_SERVER_ERROR).send(err);
    }
  }
}
