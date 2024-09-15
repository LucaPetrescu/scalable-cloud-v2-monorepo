/* eslint-disable */
import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MongooseModule } from '@nestjs/mongoose';
import { UserModule } from './api/user/user.module';
import { AuthModule } from './api/auth/auth.module';
import { MetricsModule } from './api/metrics/metrics.module';
import { PassportModule } from '@nestjs/passport';
import { HttpMetricsService } from './api/metrics/http-metrics.service';

@Module({
  imports: [
    MetricsModule,
    AuthModule,
    MongooseModule.forRoot('mongodb://localhost:27017/users'),
    PassportModule.register({ defaultStrategy: 'jwt' }),
    UserModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
