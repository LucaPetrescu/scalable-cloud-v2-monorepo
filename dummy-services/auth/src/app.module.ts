/* eslint-disable */
import { MiddlewareConsumer, Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MongooseModule } from '@nestjs/mongoose';
import { UserModule } from './api/user/user.module';
import { AuthModule } from './api/auth/auth.module';
import { MetricsModule } from './api/metrics/metrics.module';
import { PassportModule } from '@nestjs/passport';
import { DatabaseService } from './api/database/database.service';
import { HttpMetricsMiddleware } from './api/middlewares/http-metrics.middleware';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule.forRoot(),
    MetricsModule,
    AuthModule,
    MongooseModule.forRoot(process.env.MONGODB_URI),
    PassportModule.register({ defaultStrategy: 'jwt' }),
    UserModule,
  ],
  controllers: [AppController],
  providers: [AppService, DatabaseService],
})
export class AppModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(HttpMetricsMiddleware).forRoutes('*');
  }
}
