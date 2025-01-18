import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DatabaseService } from './api/database/database.service';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductModule } from './api/product/product.module';
import { MetricsModule } from './api/metrics/metrics.module';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/products'),
    ProductModule,
    MetricsModule,
  ],
  controllers: [AppController],
  providers: [AppService, DatabaseService],
})
export class AppModule {}
